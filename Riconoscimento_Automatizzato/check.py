import numpy as np
import pandas as pd
from pathlib import Path
import os.path
import tensorflow as tf
from sklearn.model_selection import train_test_split
import sys

df = pd.read_csv('./train.csv')
df.head()


df.shape

df.species.nunique()

df.species.value_counts().sort_index()
df['species'].value_counts().sort_values(ascending=True).plot(kind='barh')

image_dir = Path('./Training')
filepaths = list(image_dir.glob(r'**/*.jpg'))
labels = list(map(lambda x: os.path.split(os.path.split(x)[0])[1], filepaths))
filepaths = pd.Series(filepaths, name='Filepath').astype(str)
labels = pd.Series(labels, name='Label')
image_df = pd.concat([filepaths, labels], axis=1)

image_df

image_df['Label'].value_counts()

train_df, test_df = train_test_split(image_df, train_size=0.8, shuffle=True, random_state=1)

train_generator = tf.keras.preprocessing.image.ImageDataGenerator(
    preprocessing_function=tf.keras.applications.mobilenet_v2.preprocess_input,
)

train_images = train_generator.flow_from_dataframe(
    dataframe=train_df,
    x_col='Filepath',
    y_col='Label',
    target_size=(224, 224),
    color_mode='rgb',
    class_mode='categorical',
    batch_size=32,
    shuffle=True,
    seed=42,
    subset='training'
)

from keras.models import load_model
from keras_preprocessing.image import load_img,img_to_array
model1 = load_model('./WCvF.h5',compile=False)

lab = train_images.class_indices
lab={k:v for v,k in lab.items()}

ris="Risultato: "

def output(location):
    img=load_img(location,target_size=(224,224,3))
    img=img_to_array(img)
    img=img/255
    img=np.expand_dims(img,[0])
    answer=model1.predict(img)
    y_class = answer.argmax(axis=-1)
    y = " ".join(str(x) for x in y_class)
    y = int(y)
    res = lab[y]
    return res
    

args = sys.argv[1:]

# Use the arguments
for arg in args:
    img='/var/www/html/Tesi/Sito/img/temp/'+arg
    pic=load_img(img, target_size=(224,224,3))
    ris = ris + output(img) + "-"

print(ris)




