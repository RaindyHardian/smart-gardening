package setting

import (
	"log"
	"os"

	"github.com/joho/godotenv"
)

type Server struct {
	Port                      string
	GoogleStorageBucket       string
	GoogleProjectID           string
	GoogleFirestoreCollection string
}

var ServerSetting = &Server{}

func init() {
	if err := godotenv.Load(); err != nil {
		log.Printf("[INFO] %v", err)
	}
}

func Setup() {
	ServerSetting.Port = os.Getenv("PORT")
	ServerSetting.GoogleStorageBucket = os.Getenv("GOOGLE_STORAGE_BUCKET")
	ServerSetting.GoogleProjectID = os.Getenv("GOOGLE_PROJECT_ID")
	ServerSetting.GoogleFirestoreCollection = os.Getenv("GOOGLE_FIRESTORE_COLLECTION")
}
