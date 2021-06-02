package setting

import (
	"fmt"
	"log"
	"os"
	"path"
	"path/filepath"
	"runtime"

	"github.com/joho/godotenv"
)

type Server struct {
	APIKey                    string
	Port                      string
	GoogleStorageBucket       string
	GoogleProjectID           string
	GoogleFirestoreCollection string
	URLPrediction             string
}

var ServerSetting = &Server{}

func init() {
	if err := godotenv.Load(rootDir()); err != nil {
		log.Printf("[INFO] %v", err)
	}
}

func rootDir() string {
	_, b, _, _ := runtime.Caller(0)
	d := path.Join(path.Dir(b))
	return fmt.Sprint(filepath.Dir(d), "/.env")
}

func Setup() {
	ServerSetting.Port = os.Getenv("PORT")
	ServerSetting.GoogleStorageBucket = os.Getenv("GOOGLE_STORAGE_BUCKET")
	ServerSetting.GoogleProjectID = os.Getenv("GOOGLE_PROJECT_ID")
	ServerSetting.GoogleFirestoreCollection = os.Getenv("GOOGLE_FIRESTORE_COLLECTION")
	ServerSetting.APIKey = os.Getenv("API_KEY")
	ServerSetting.URLPrediction = os.Getenv("URL_PREDICTION")
}
