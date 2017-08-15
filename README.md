# Yet Another Banking System

Implementation of simple banking system for money transfers between internal users/accounts

This system supports several operations, such as deposit, withdraw and transfer money.

## Usage
Launch application on specified port `java -jar yabs-server.jar 31337`

## Messages
Communication with application is based on JSON messages. There are two types of response messages: _Ok_ and _Error_.

### Ok Response
```json
{
  "status": "OK",
  "result": {}
}
```

`result` will contain JSON object based on endpoint, see [endpoints](#endpoints) section for details.

### Error
```json
{
  "status": "ERROR",
  "code": 42,
  "message": "Unknown Server Error"
}
```

## Endpoints
### Registration
For generation new account create **POST** request to `/registration`

##### Request
You can specify initial balance in `balance` field or it assumed as to equal zero
```json
{
  "balance": 0.0
}
```
##### Response
In the case of success result you will receive new account object with current balance
```json
{
  "uuid": "00000000-0000-0000-0000-000000000000",
  "balance": 0.0
}
```
### Deposit
For deposition money to account create **POST** request to `/deposit/{uuid}`, where `{uuid}` is account uuid received in registration response.

##### Request
You can and obliged to specify the non-negative amount in `amount` field
```json
{
  "amount": 0.0
}
```
##### Response
In the case of success result you will receive account object with balance updated
```json
{
  "uuid": "00000000-0000-0000-0000-000000000000",
  "balance": 0.0
}
```

### Withdraw
To withdraw some money from account create **POST** request to `/withdraw/{uuid}`, where `{uuid}` is account uuid received in registration response.

##### Request
You can and obliged to specify the non-negative amount and less or equal to current account balance in `amount` field
```json
{
  "amount": 0.0
}
```
##### Response
In the case of success result you will receive account object with balance updated
```json
{
  "uuid": "00000000-0000-0000-0000-000000000000",
  "balance": 0.0
}
```

### Transfer
To send money to another existing account in system create **POST** request to `/transfer/{uuid}`, where `{uuid}` is account uuid received in registration response.

##### Request
You can and obliged to specify recipient account uuid in `recipient` field
You can and obliged to specify the non-negative amount and less or equal to current account balance in `amount` field
```json
{
  "recipient": "00000000-0000-0000-0000-000000000000",
  "amount": 0.0
}
```
##### Response
In the case of success result you will receive account object with balance updated
```json
{
  "uuid": "00000000-0000-0000-0000-000000000000",
  "balance": 0.0
}
```

### Balance
If you want to check your current balance create **GET** request to `/balance/{uuid}`

##### Response
In the case of success result you will receive account object with balance updated
```json
{
  "uuid": "00000000-0000-0000-0000-000000000000",
  "balance": 0.0
}
```
