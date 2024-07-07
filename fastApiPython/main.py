from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from twilio.rest import Client
from keys import auth_token
from keys import account_sid
from keys import twilio_number

app = FastAPI()

twilio_phone_number = "your_twilio_phone_number"

client = Client(account_sid, auth_token)

class SMSBody(BaseModel):
    to: str
    code: str

@app.post("/sms")
async def send_sms(sms_body: SMSBody):
    try:
        total_message = 'El codigo de verificacion para tu cuenta en DebtApp es: ' + sms_body.code
        message = client.messages \
            .create(
            body=total_message,
            from_=twilio_number,
            to=sms_body.to
        )
        print(message.sid)
        return {"status": "success", "message_sid": message.sid}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="192.168.3.89", port=8003)

