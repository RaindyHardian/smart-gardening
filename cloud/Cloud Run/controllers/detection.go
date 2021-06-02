package controllers

import (
	"context"
	"fmt"
	"io"
	"log"
	"mime/multipart"
	"net/http"
	"path/filepath"
	"regexp"
	"strconv"
	"time"

	"bangkit.academy/smartgardening/cloudrun/services/firestore"
	"bangkit.academy/smartgardening/cloudrun/services/storage"
	"bangkit.academy/smartgardening/cloudrun/setting"
	"bangkit.academy/smartgardening/cloudrun/util"
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
)

type plantImage struct {
	Image *multipart.FileHeader `form:"image" binding:"required"`
}

func Detection(c *gin.Context) {
	var form plantImage

	if err := c.ShouldBind(&form); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": err.Error(),
			"title": "validation failed",
		})
		return
	}

	matched, _ := regexp.MatchString("image", form.Image.Header.Values("content-type")[0])
	if !matched {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": "file not image",
		})
		return
	}

	imChan := make(chan multipart.File)
	go func() {
		image, err := openImage(*form.Image)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{
				"error": err.Error(),
				"title": "failed open image",
			})
		}
		imChan <- image
	}()
	image := <-imChan
	defer image.Close()

	mlChan := make(chan string)
	go func() {
		ml, err := predictionReq(image, setting.ServerSetting.URLPrediction, form.Image.Filename)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{
				"error": err.Error(),
				"title": "request",
			})
			return
		}
		mlChan <- ml
	}()
	ml := <-mlChan

	storChan := make(chan string)
	go func() {
		object := fmt.Sprint(generateName(), filepath.Ext(form.Image.Filename))
		bucket := setting.ServerSetting.GoogleStorageBucket
		storage, err := storage.Upload(image, object, bucket)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{
				"error": err.Error(),
				"title": "upload to cloud storage",
			})
			return
		}
		storChan <- storage
	}()
	storage := <-storChan

	plantChan := make(chan map[string]interface{})
	go func() {
		client := firestore.CreateClient(context.Background())
		defer client.Close()
		plant, err := firestore.GetData(context.Background(), client, ml)
		if err != nil {
			c.JSON(http.StatusNotFound, gin.H{
				"detail": err.Error(),
				"error":  "get data from database failed",
			})
			return
		}
		plantChan <- plant
	}()
	plant := <-plantChan

	c.JSON(http.StatusOK, gin.H{
		"plant":   plant,
		"storage": storage,
	})
}

func openImage(file multipart.FileHeader) (multipart.File, error) {
	image, err := file.Open()
	if err != nil {
		return nil, fmt.Errorf("failed open image: %v", err)
	}
	return image, nil
}

func generateName() string {
	unixtime := strconv.Itoa(int(time.Now().UTC().Unix()))
	uuid := uuid.NewString()
	return fmt.Sprintf("%v-%v", uuid, unixtime)
}

func predictionReq(file multipart.File, url string, filename string) (string, error) {
	r, w := io.Pipe()
	defer r.Close()
	m := multipart.NewWriter(w)

	go func() {
		defer w.Close()
		defer m.Close()
		part, err := m.CreateFormFile("file", filename)
		if err != nil {
			log.Printf("create form file: %v", err)
			w.CloseWithError(err)
			return
		}
		if _, err := io.Copy(part, file); err != nil {
			log.Printf("copy: %v", err)
			w.CloseWithError(err)
			return
		}
	}()

	resp, err := http.Post(url, m.FormDataContentType(), r)
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return "", fmt.Errorf("request error: %v", resp.Body)
	}

	body, err := util.Bodytojson(resp.Body)
	if err != nil {
		return "", fmt.Errorf("body to json: %v", err)
	}

	return fmt.Sprintf("%v", body["result"]), err
}
