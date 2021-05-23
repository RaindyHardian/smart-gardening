package controllers

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

func IndexGet(c *gin.Context) {
	c.JSON(http.StatusOK, gin.H{
		"message": "Bangkit Capstone B21-CAP0251",
	})
}
