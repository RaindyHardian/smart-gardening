package setting

import (
	"log"
	"os"

	"github.com/joho/godotenv"
)

type Server struct {
	HttpPort                  string
	GoogleStorageBucket       string
	GoogleProjectID           string
	GoogleFirestoreCollection string
}

var ServerSetting = &Server{}

func init() {
	if err := godotenv.Load(); err != nil {
		log.Fatal(err)
	}
}

func Setup() {
	ServerSetting.HttpPort = os.Getenv("HTTP_PORT")
	ServerSetting.GoogleStorageBucket = os.Getenv("GOOGLE_STORAGE_BUCKET")
	ServerSetting.GoogleProjectID = os.Getenv("GOOGLE_PROJECT_ID")
	ServerSetting.GoogleFirestoreCollection = os.Getenv("GOOGLE_FIRESTORE_COLLECTION")
}
