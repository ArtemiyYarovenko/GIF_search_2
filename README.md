# GIF_search
Курсовой проект по учебному курсу "Проектирование мобильных клиент-серверных систем (09.04.04) ~О~В~З"

На текущий момент сделано:

* Отображение Gif-изображений через ImageView через библиотеку Glide

* Настроен API запрос к сервису GIPHY на загурзку набирающих популярность GIF-изображений и GIF-изображений по ключевому слову через библиотеку Retrofit

* Описаны и встроены Recycler View адаптеры для отображения изображений с базы данных и с http-запроса

* Полностью построена объектная модель входящего Response на Api-вызов (см. Object)

* Описан инструментарий RoomDatabase

* Дописаны конвертеры для составных объектов, RoomDatabase включена

* Добавил строку ввода для поиска изображений по ключевым словам

* Сделал динамическую модель вызовов Retrofit (через Url) для поиска изображений по ключевым словам

Планируется:

* Добавить возможность выбора способа отображения GIF-изображений (сколько столбцов, какого разрешения изображения и т.д.)

* Добавить локализацию ru и en

