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

	if matched, _ := regexp.MatchString("image/j", form.Image.Header.Values("content-type")[0]); !matched {
		c.JSON(http.StatusBadRequest, gin.H{
			"content-type": form.Image.Header.Values("content-type")[0],
			"error":        "file not image or not supported",
		})
		return
	}

	object := fmt.Sprint(generateName(), filepath.Ext(form.Image.Filename))
	bucket := setting.ServerSetting.GoogleStorageBucket
	storage, err := storage.Upload(*form.Image, object, bucket, form.Image.Header.Values("content-type")[0])
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"error": err.Error(),
			"title": "upload to cloud storage",
		})
		return
	}

	ml, err := predictionReq(*form.Image, setting.ServerSetting.URLPrediction, storage)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"error": err.Error(),
			"title": "request",
		})
		return
	}

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

	c.JSON(http.StatusOK, gin.H{
		"plant":   plant,
		"storage": storage,
	})
}

func generateName() string {
	unixtime := strconv.Itoa(int(time.Now().UTC().Unix()))
	uuid := uuid.NewString()
	return fmt.Sprintf("%v-%v", unixtime, uuid)
}

func predictionReq(file multipart.FileHeader, url string, filename string) (result string, err error) {
	r, w := io.Pipe()
	defer r.Close()
	m := multipart.NewWriter(w)
	img, err := util.OpenImage(file)
	if err != nil {
		return "", fmt.Errorf("failed open image request: %v", err)
	}
	defer img.Close()

	go func() {
		defer w.Close()
		defer m.Close()
		part, err := m.CreateFormFile("file", filename)
		if err != nil {
			log.Printf("create form file: %v", err)
			w.CloseWithError(err)
			return
		}
		if _, err := io.Copy(part, img); err != nil {
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
