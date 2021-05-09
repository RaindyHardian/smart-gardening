package api

import (
	"mime/multipart"
	"net/http"
	"regexp"

	"bangkit.academy/smartgardening/cloudrun/services/gcs"
	"github.com/gin-gonic/gin"
)

type flower struct {
	Image *multipart.FileHeader `form:"image" binding:"required"`
}

func FlowerEndpoint(c *gin.Context) {
	var form flower

	if err := c.ShouldBind(&form); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": "validation failed",
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

	file, err := form.Image.Open()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"error": "open file error",
		})
		return
	}
	defer file.Close()
	resp, err := gcs.Upload(file, form.Image.Header.Values("content-type")[0])
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"error": "upload to cloud storage",
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"imageName": form.Image.Filename,
		"size":      form.Image.Size,
	})
}
