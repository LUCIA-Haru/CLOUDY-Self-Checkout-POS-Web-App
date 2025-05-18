// export function decodeToken(token: string): any {
//   try {
//     const payload = token.split('.')[1]; // Extract the payload part of the JWT
//     return JSON.parse(atob(payload)); // Decode base64 and parse JSON
//   } catch (error) {
//     console.error('Invalid token:', error);
//     return null;
//   }
// }
export function decodeToken(token: string): any {
  try {
    // Split the token into its three parts: header, payload, and signature
    const base64Url = token.split('.')[1];
    if (!base64Url) {
      throw new Error('Invalid token format');
    }

    // Decode the Base64-encoded payload
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    const decodedToken = JSON.parse(jsonPayload);
    return decodedToken;
  } catch (error) {
    if (error instanceof Error) {
      console.error('Error decoding token:', error.message);
    } else {
      console.error('Error decoding token:', error);
    }
    return null;
  }
}
export function isTokenExpired(token: string): boolean {
  const decodedToken = decodeToken(token);
  if (!decodedToken || !decodedToken.exp) return true;

  const currentTime = Math.floor(Date.now() / 1000); // Current time in seconds
  return decodedToken.exp < currentTime; // Check if token is expired
}
