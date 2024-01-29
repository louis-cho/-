import * as RSA from './rsa.js';

let modulus = null;
let exponent = null;
export let rsa = new RSA.RSAKey();

// Empty function for the "공개키 받아오기" button
async function getPublicKey() {
  // Add your logic here or leave it empty
  const serverUrl = "http://localhost:8081/user/login"; // Update the URL accordingly

  // Create a data object with the user credentials
  const data = {
    "type": "getPublicKey"
  };

  // Send a POST request to the server
  await fetch(serverUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  })
    .then(response => response.json())
    .then(result => {
      // Handle the result from the server as needed
      console.log("서버로부터 받은 공개키 >> " + result.modulus + "," + result.exponent);
      modulus = result.modulus;
      exponent = result.exponent;

      rsa.setPublic(modulus, exponent);
    })
    .catch(error => {
      console.error('Error:', error);
    });
}

export async function sendAESKey(aesKeyData) {
  let encryptedKey = rsa.encrypt(aesKeyData);

  const serverUrl = "http://localhost:8081/user/login"; // Update the URL accordingly

  const data = {
    "type": "setAESKey",
    "key": encryptedKey
  };

  // Send a POST request to the server
  await fetch(serverUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  })
    .then(response => response.json())
    .then(result => {
      // Handle the result from the server as needed
      console.log(result);
    })
    .catch(error => {
      console.error('Error:', error);
    });
}

async function login(id, pw) {
    // Add your logic here or leave it empty
    const serverUrl = "http://localhost:8081/user/login"; // Update the URL accordingly

    // Create a data object with the user credentials
    const data = {
      type: "login",
      id: id,
      pw: rsa.encrypt(pw)
    };

    // Send a POST request to the server
    await fetch(serverUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    })
      .then(response => response.json())
      .then(result => {
        // Handle the result from the server as needed
        console.log("서버로부터 받은 결과 >> " + result);
      })
      .catch(error => {
        console.error('Error:', error);
      });
}

async function register(id, pw) {
  const serverUrl = "http://localhost:8081/user/register"; // Update the URL accordingly

  // Create a data object with the user credentials
  const data = {
    type: "register",
    id: id,
    pw: rsa.encrypt(pw)
  };

  // Send a POST request to the server
  await fetch(serverUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  })
    .then(response => response.json())
    .then(result => {
      if(result.token) {
        // 토큰 관리 js 파일의 기능을 호출하기
      }
    })
    .catch(error => {
      console.error('Error:', error);
    });

}

function handleRegister() {
  let id = document.getElementById("id").value;
  let pw = document.getElementById("pw").value;

  register(id, pw);
}


export { getPublicKey, modulus, exponent, handleRegister, register, login };
