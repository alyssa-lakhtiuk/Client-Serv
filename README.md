# Client-Serv
Labs for Client-server development
- Different labs on different branches

### Lab01
Уявимо, що ви розробляєте клієнт серверне застосування, що має обмінюватися повідомленнями по мережі. Ваші дані містять комерційну таємницю і не можуть передаватися в відкритому вигляді. Тому ви маєте розробити протокол обміну повідомленнями. Таким чином структура нашого пакету:

| Offset | Length | Mnemonic | Notes|
| ------ | ------ | ------ | ------ | 
| 00 | 1 | bMagic | Байт, що вказує на початок пакету - значення 13h (h - значить hex) |
|01 | 1 | bSrc | Унікальний номер клієнтського застосування |
|02 | 8 | bPktId | Номер повідомлення. Номер постійно збільшується. В форматі big-endian |
|10 | 4 | wLen | Довжина пакету даних big-endian |
|14 | 2 | wCrc16 | CRC16 байтів (00-13) big-endian|
|16 | wLen | bMsq | Message - корисне повідомлення|
|16+wLen2 | wCrc16 | wCrc16 | CRC16 байтів (16 до 16+wLen-1) big-endian|

Структура повідомлення (message)
| Offset | Length | Mnemonic | Notes|
| ------ | ------ | ------ | ------ | 
| 00 | 4 | cType | Код команди big-endian |
|01 | 4 | bUserId |Від кого надіслане повідомлення. В системі може бути багато клієнтів. А на кожному з цих клієнтів може працювати один з багатьох працівників. big-endian|
| 08 | wLen-8 | message | корисна інформація, можна покласти JSON як масив байтів big-endian |


### Lab02 (tic-tak-toy)

* Виправити TicTak програму, так що б вона писала 5 разів Tic-Tak
* Розширити TicTak додавши третій поток і виводити Tic-Tak-Toy

### Lab02
Так як у нас клієнт-серверна архітектура, на сервер може приходити одночасно багато конкурентних запитів і він має продукувати багато одночасних відповідей.
Ваше завдання реалізувати наступні команди:
* Взнати кількість товару на складі
* Списати певну кількість товару
* Зарахувати певну кількість товару
* Додати групу товарів
* Додати назву товару до групи
* Встановити ціну на конкретний товар

Необхідно зробити наступні класи:

1. Інтерфейс що приймає повідомлення по мережі.
2. Фейкова реалізація інтерфейсу, що генерує довільні повідомлення.
3. Клас, що в багато потоків розбирає, дешифрує та перетворює повідомлення в об'єкт домену. Після чого передає на обробник повідомлення
4. Клас, що в багато потоків приймає перетворене повідомлення та формує відповідь. Поки достатньо відповіді Ок.
5. Клас, що в багато потоків шифрує відповідь та відправляє класу, що відповідає за передачу інформації по мережі
6. Фейкова реалізація відправки, що просто виводить інформацію на екран.
7. Система має коректно завершувати роботу
8. Створити JUnit тести, що в багато потоків відправляють повідомлення

| Receiver |
| ------ | 
| + receiveMessage(): void | 

| Decryptor |
| ------ | 
| + decrypt(byte[] message): void |

| Encryptor |
| ------ | 
| + encrypt(Message message): byte[] |

| Processor |
| ------ | 
| + process(Message message): void |

| Sender |
| ------ | 
| + sendMessage(byte[] mess, InteAddress target): void |


### Lab03
Продовжити попереднє практичне. Тобто ми обмінюємося пакетами, що розроблені в попередньому практичному.

Тепер нам необхідно створити класи:

1. StoreServerTCP та StoreClientTCP
2. StoreServerUDP та StoreClientUDP

Відповідно вони мають працювати по протоколу TCP та протоколу UDP

Ви маєте запустити декілька клієнтів і один сервер та протестувати роботу.

Маєте звернути увагу на те, що:

* при передачі по UDP пакети можуть бути втрачені тому ваш власний протокл обміну даними це має обслуговувати і робити переповтор в разі втрати даних
* при передачі даних по TCP проблемою є обрив зв'язку між клієнтом і сервером. В разі проблем сервер буде перевантажений, але ваш клієнт має коректно оброблювати дану ситуацію:
  * не відправляти пакети поки сервер не відновить роботу
  * розуміти, що сервер зараз не доступний і пробувати відновити з'єднання
