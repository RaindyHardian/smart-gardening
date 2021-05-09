package setting

import (
	"log"
	"os"

	"github.com/joho/godotenv"
)

type Server struct {
	RunMode  string
	HttpPort string
}

var ServerSetting = &Server{}

func init() {
	if err := godotenv.Load(); err != nil {
		log.Fatal("Error loading .env file")
	}
}

func Setup() {
	ServerSetting.RunMode = os.Getenv("GIN_MODE")
	ServerSetting.HttpPort = os.Getenv("HTTP_PORT")
}
