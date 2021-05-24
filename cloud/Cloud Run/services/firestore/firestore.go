package firestore

import (
	"context"
	"errors"
	"fmt"
	"log"

	"bangkit.academy/smartgardening/cloudrun/setting"
	"cloud.google.com/go/firestore"
)

func CreateClient(ctx context.Context) *firestore.Client {
	projectID := setting.ServerSetting.GoogleProjectID

	client, err := firestore.NewClient(ctx, projectID)
	if err != nil {
		log.Fatalf("Failed to create firestore client: %v", err)
	}

	return client
}

func AddData(ctx context.Context, client *firestore.Client, document string, data interface{}) error {
	_, err := client.Collection(setting.ServerSetting.GoogleFirestoreCollection).Doc(document).Set(ctx, data)
	if err != nil {
		return fmt.Errorf("failed add data firestore: %v", err)
	}
	return nil
}

func GetData(ctx context.Context, client *firestore.Client, name string) (map[string]interface{}, error) {
	doc, err := client.Collection(setting.ServerSetting.GoogleFirestoreCollection).Where("name", "==", name).Documents(ctx).GetAll()
	if err != nil {
		return nil, err
	}
	if len(doc) == 0 {
		return nil, errors.New("data not found")
	}
	return doc[0].Data(), nil
}
