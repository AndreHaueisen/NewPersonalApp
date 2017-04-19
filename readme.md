# Project 4 - *OkGo*

**OkGo** is an android app that allows user to find personal trainers and schedule classes at a give location. The app utilizes [Firebase](https://firebase.google.com/) as backend service. The app includes both user and personal trainer interfaces.
This app validates personal trainers profiles checking for their CREF (Conselho Regional de Educação Física or Regional Council of Physical Education)
number sending POST requests to [Conselho Federal de Educação Física (Federal Council of Physical Education)](http://www.confef.org.br/extra/registrados/) website.
The app has a widget that is not yet fully functional (view **TODOS & Known Issues** ).

Time spent: **8** months spent in total

## Features

### User's Side

User side has the following functionalities:

* [x] Creates **user profile**
* [x] Can **change edit profile** picture, gym location and main objective
  * [x] Change profile picture
  * [x] Change gym location
  * [x] Change main objective
  * [x] Profile changes are saved on Firebase
* [x] Can **search for a personal trainer**
  * [x] Make a general search
  * [x] Make a specific search using the personal trainer's email or CREF number
* [x] Can **check for confirmed and unconfirmed schedules classes**
* [x] Can **cancel confirmed and unconfirmed classes**
* [x] Can **review classes in the past** using a grade from 0 to 5 and comments
* [x] Can **log out**

### Personal Trainer's Side

Personal side has the following functionalities:

* [x] Can create a CREF verified personal profile
* [x] Can **change edit profile** picture, profile background picture
  * [x] Change profile picture
  * [x] Change background profile picture
  * [x] Change gym location
  * [x] Change main specialties
  * [x] Change charge for each 15 minutes of class
  * [x] Profile changes are saved on Firebase
* [x] Can **create a week based schedule** which users must obey to be able to schedule the class
* [x] Can **review clients comments and grades**
* [x] Can **log out**

### For Udacity Instructors

Reviewer, you can test the personal trainer side using credentials from this website (Federal Council of Physical Education)](http://www.confef.org.br/extra/registrados/)
To make it easier, I put bellow some credentials chosen randomly

* Credential one
    * Name as in CREF register: ALESSANDRE DA ROCHA
    * CREF number: 2831
    * CREF STATE: SERGIPE
* Credential two
    * Name as in CREF register: RENATO CEZAR ZAINE
    * CREF number: 23352
    * CREF STATE: PARANÁ
* Credential one
    * Name as in CREF register: LUCAS GOMES DE ARAUJO
    * CREF number: 3248
    * CREF STATE: GOIÁS

To test the client side simply don't select "I'm a personal" checkbox

Worked on this for 8 months. As it grew I noticed the problems one has when dealing with
big apps. Code becomes spaghetti. Debug becomes a pain. To avoid those problems in future projects I
chased some new skills. Learned MVP architecture, learned how to use dagger and reactive
programming with RxJava. Since I did not had these skills ate the beginning I could'd use them this time.
Also you will notice some classes in Kotlin. That is me learning the language. Sorry about the size of the app.


### Notes

* App have some classes written in Kotlin since I'm learning it
* Classes can be scheduled on a 15 minutes base. For example: 15:00 or 15:15 or 15:30 or 15:45.
* Classes has the minimum duration of 45 minutes and a maximum duration of 120 minutes.
* Price tag personal trainers set is for a 15 minute charge. For example: charge of $10 and class of 45 minutes will cost 3*10 = $30


## Open-source libraries used

- [Recyclerview Animators](https://github.com/wasabeef/recyclerview-animators) - RecyclerView Animators is an Android library that allows developers to easily create RecyclerView with animations.
- [Glide](https://github.com/bumptech/glide) - Glide is a fast and efficient open source media management and image loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface.
- [Touch Boarder Weekdays Buttons Bar](https://github.com/TouchBoarder/weekdays-buttons-bar) - For Personal Trainer week schedule
- [CircleImageView](https://github.com/hdodenhof/CircleImageView#circleimageview) - A fast circular ImageView perfect for profile images.
- [MaterialRangeBar](https://github.com/oli107/material-range-bar)
- [Android-SpinKit](https://github.com/ybq/Android-SpinKit) - Confirming and canceling classes animations
- [MaterialBadgeTextView](https://github.com/matrixxun/MaterialBadgeTextView#materialbadgetextview) - Badge for classes in need of review
- [joda-time-android](https://github.com/dlew/joda-time-android) - This library is a version of Joda-Time built with Android in mind.
- [Light Calendar View](https://github.com/recruit-mp/LightCalendarView) - A lightweight monthly calendar view for Android, fully written in Kotlin. Designed to meet the minimum demands for typical calendars.
- [WheelView](https://github.com/venshine/WheelView) - Android wheel control, based on the ListView implementation, you can customize the style.
- [Toasty](https://github.com/GrenderG/Toasty) - The usual Toast, but with steroids.
- [DesertPlaceholder](https://github.com/JetradarMobile/desertplaceholder) - Animated placeholder in desert style
- [Butter Knife](https://github.com/JakeWharton/butterknife) - View injection
- [Android Image Cropper](https://github.com/ArthurHub/Android-Image-Cropper) - Powerful (Zoom, Rotation, Multi-Source), customizable (Shape, Limits, Style), optimized (Async, Sampling, Matrix) and simple image cropping library for Android.

## TODOS & Knows Issues
* Make onboarding screens
* Fix widget
* Implements push notifications
* Make views more dynamic
* Enhance design

## License

    Copyright [2017] [Andre Haueisen Batista de Lima]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.