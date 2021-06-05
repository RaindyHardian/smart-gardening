package controllers

import (
	"context"
	"net/http"

	"bangkit.academy/smartgardening/cloudrun/services/firestore"
	"github.com/gin-gonic/gin"
)

func GetDataByName(c *gin.Context) {
	client := firestore.CreateClient(context.Background())
	defer client.Close()
	name := c.Query("name")
	data, err := firestore.GetDataPlant(context.Background(), client, name)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{
			"detail": err.Error(),
			"error":  "get data from database failed",
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"plant": data,
	})
}
