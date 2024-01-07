# [Xml Version](https://github.com/mahdiasd/DialogFilePicker)

# Bottom Dialog Android Picker

[![](https://jitpack.io/v/mahdiasd/BottomDialogFilePicker.svg)](https://jitpack.io/#mahdiasd/BottomDialogFilePicker)

Bottom dialog picker like telegram for all version of android (1 ... , 10 , 11 , 12 , 13)

Take picture with camera and save to storage

Search in Files

Support android 10+

Expandable and scrollable dialog

Full Customisable (Color , text , minimum and maximum selected file size , ...)

No required runtime permission


## Screenshots

![demo](https://raw.githubusercontent.com/mahdiasd/BottomDialogFilePicker/master/screenshot/1.png)
![demo](https://raw.githubusercontent.com/mahdiasd/BottomDialogFilePicker/master/screenshot/2.png)
![demo](https://raw.githubusercontent.com/mahdiasd/BottomDialogFilePicker/master/screenshot/3.png)
![demo](https://raw.githubusercontent.com/mahdiasd/BottomDialogFilePicker/master/screenshot/4.png)
![demo](https://raw.githubusercontent.com/mahdiasd/BottomDialogFilePicker/master/screenshot/5.png)

## Installation

#### Step 1. Add the JitPack repository to your build file

Install my project with gradle
Add it in your root build.gradle at the end of repositories:


```bash
  allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
#### Step 2. Add the dependency

```bash
  dependencies {
      implementation 'com.github.mahdiasd:ComposeBottomDialogFilePicker:1.0.1'
	}
```
## Ho To Use

```
 val isShowButtomDialog = remember {  mutableStateOf(true)   }

 val config = PickerConfig(
            currentType = PickerType.Image,
            storageTitle = "حافظه دستگاه",
            storageDescription = "برای انتخاب فایل از فایل منیجر دستگاه",
            galleryTitle = "گالری",
            galleryDescription = "برای انتخاب فایل از گالری دستگاه",
            supportRtl = true,
            maxSelection = 12,
            searchTextHint = "جستجو",
            searchTextHintStyle = TextStyle(textAlign = TextAlign.Right)
        )

 FilePickerDialog(
                            config = config,
                            onDismissDialog = {
                                isShowButtomDialog.value = false
                            },
                            selectedFiles = {
                                it.printToLog("selectedFiles")
                            }
                        )
```

## LICENCE
```
Copyright 2022 Mahdi Asadollahpour BottomDialogFilePicker

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```

### | ~~~~ Thank you for your support of my project and star it ~~~~ |
