package util

import (
	"fmt"
	"mime/multipart"
)

func OpenImage(file multipart.FileHeader) (multipart.File, error) {
	image, err := file.Open()
	if err != nil {
		return nil, fmt.Errorf("failed open image: %v", err)
	}
	return image, nil
}
