import numpy as np
from tensorflow.keras.preprocessing import image
from tensorflow.keras.models import load_model

model = load_model('5_model_akurasi_93_91_93.h5')
img = image.load_img(path='../plant_dataset/validation/sirih_gading/sirih_gading (5).jpg', target_size=(160,160))
img = image.img_to_array(img)
img = np.expand_dims(img, axis=0)
result = model.predict(img)
print(result)

y_pred = np.argmax(result, axis=1)
print(y_pred[0])

if y_pred[0] == 0:
    print("aglonema")
elif y_pred[0] == 1:
    print("janda_bolong")
elif y_pred[0] == 2:
    print("kuping_gajah")
elif y_pred[0] == 3:
    print("lidah_mertua")
elif y_pred[0] == 4:
    print("sirih_gading")
elif y_pred[0] == 5:
    print("tanaman_lipstik_gantung")
elif y_pred[0] == 6:
    print("tanaman_suplir")