package routers

import (
	"bangkit.academy/smartgardening/cloudrun/routers/api"
	"github.com/gin-gonic/gin"
)

func Setup() *gin.Engine {
	r := gin.New()
	r.Use(gin.Logger())
	r.Use(gin.Recovery())

	r.GET("/", api.IndexGet)
	r.POST("/flower", api.FlowerEndpoint)

	return r
}
