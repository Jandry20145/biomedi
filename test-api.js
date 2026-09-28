const http = require('http');

async function test() {
  const loginRes = await fetch('http://localhost:18080/api/v1/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: 'storres', password: '1234' })
  });
  const loginData = await loginRes.json();
  const token = loginData.accessToken;
  
  console.log("Token received:", token ? "yes" : "no");
  
  const res = await fetch('http://localhost:18080/api/clinical-records/2/consultations/3/odontogram/events', {
    method: 'POST',
    headers: { 
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      pieceCode: 'TOOTH_02',
      status: 'ENDODONCIA',
      surfaces: ['PALATINA'],
      observations: 'Test'
    })
  });
  
  console.log("Status:", res.status);
  const text = await res.text();
  console.log("Response:", text);
}

test();
