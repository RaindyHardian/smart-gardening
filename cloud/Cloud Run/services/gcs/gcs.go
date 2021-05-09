package gcs

import (
	"context"
	"fmt"
	"io"
	"log"
	"mime/multipart"
	"time"

	"cloud.google.com/go/storage"
)

func Upload(image multipart.File, contentType string) (string, error) {
	bucket := "lala"
	ctx := context.Background()
	client, err := storage.NewClient(ctx)
	if err != nil {
		log.Fatalf("Failed to create client: %v", err)
	}
	defer client.Close()

	ctx, cancel := context.WithTimeout(ctx, time.Second*60)
	defer cancel()

	wc := client.Bucket(bucket).Object(object).NewWriter(ctx)
	if _, err = io.Copy(wc, image); err != nil {
		return fmt.Errorf("failed copy: %v", err)
	}
}
