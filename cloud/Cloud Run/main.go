package main

import (
	"fmt"
	"log"

	"bangkit.academy/smartgardening/cloudrun/routers"
	"bangkit.academy/smartgardening/cloudrun/setting"
	"github.com/gin-gonic/gin"
)

func init() {
	setting.Setup()
}

func main() {
	port := fmt.Sprintf(":%s", setting.ServerSetting.HttpPort)

	gin.SetMode(setting.ServerSetting.RunMode)
	r := routers.Setup()

	log.Printf("[INFO] server listening on %s", port)

	r.Run(port)
}
