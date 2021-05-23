package main

import (
	"fmt"
	"log"

	"bangkit.academy/smartgardening/cloudrun/routers"
	"bangkit.academy/smartgardening/cloudrun/setting"
)

func init() {
	setting.Setup()
}

func main() {
	port := fmt.Sprintf(":%s", setting.ServerSetting.Port)

	r := routers.Setup()

	log.Printf("[INFO] server listening on %s", port)

	r.Run(port)
}
