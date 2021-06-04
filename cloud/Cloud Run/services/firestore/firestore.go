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

func GetData(ctx context.Context, client *firestore.Client, name string) (plant map[string]interface{}, err error) {
	docPlant, err := client.Collection(setting.ServerSetting.GoogleFirestoreCollection).Where("name", "==", name).Documents(ctx).GetAll()
	if err != nil {
		return nil, fmt.Errorf("firestore query plant: %v", err)
	}
	if len(docPlant) == 0 {
		return nil, errors.New("data plant not found")
	}

	plant = docPlant[0].Data()

	disease := map[string]interface{}{}
	for _, s := range plant["disease"].([]interface{}) {
		docDisease, err := client.Collection("disease").Doc(s.(string)).Get(ctx)
		if err != nil {
			return nil, fmt.Errorf("firestore query disease: %v", err)
		}
		disease[s.(string)] = docDisease.Data()
	}
	plant["disease"] = disease

	return plant, nil
}
