package controllers

import (
	"fmt"
	"mime/multipart"
	"net/http"
	"path/filepath"
	"regexp"
	"strconv"
	"time"

	"bangkit.academy/smartgardening/cloudrun/services/storage"
	"bangkit.academy/smartgardening/cloudrun/setting"
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

	image, err := openImage(*form.Image)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"error": err.Error(),
			"title": "failed open image",
		})
	}

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

	c.JSON(http.StatusOK, gin.H{
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
