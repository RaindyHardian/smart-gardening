package routers

import (
	"bangkit.academy/smartgardening/cloudrun/controllers"
	"github.com/gin-gonic/gin"
)

func Setup() *gin.Engine {
	r := gin.New()
	r.Use(gin.Logger())
	r.Use(gin.Recovery())

	r.GET("/", controllers.IndexGet)
	r.POST("/detection", controllers.Detection)
	r.GET("/plant", controllers.GetDataByName)

	return r
}
