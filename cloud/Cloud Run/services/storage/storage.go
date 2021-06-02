package storage

import (
	"context"
	"fmt"
	"io"
	"mime/multipart"
	"time"

	"bangkit.academy/smartgardening/cloudrun/util"
	"cloud.google.com/go/storage"
)

func Upload(image multipart.FileHeader, object string, bucket string, contentType string) (string, error) {
	ctx := context.Background()
	client, err := storage.NewClient(ctx)
	if err != nil {
		return "", fmt.Errorf("failed to create client: %v", err)
	}
	defer client.Close()

	ctx, cancel := context.WithTimeout(ctx, time.Second*60)
	defer cancel()

	img, err := util.OpenImage(image)
	if err != nil {
		return "", fmt.Errorf("failed open image storage: %v", err)
	}
	defer img.Close()

	wc := client.Bucket(bucket).Object(object).NewWriter(ctx)
	if _, err = io.Copy(wc, img); err != nil {
		return "", fmt.Errorf("failed copy: %v", err)
	}
	if err := wc.Close(); err != nil {
		return "", fmt.Errorf("Writer.Close: %v", err)
	}

	return wc.Attrs().Name, nil
}
