import CryptoJS from "crypto-js";
import AES from "crypto-js/aes";

const key = process.env.REACT_APP_KEY;

export function encrypt(s: string|undefined): string {
    if (!s || !key) {
        return "";
    }
    const enc = AES.encrypt(s, key).toString();
    const encReplace = enc.split("/").join("-s1la2sh-");
    return encReplace;
}

export function decrypt(s: string|undefined): string {
    if (!s || !key) {
        return "";
    }
    const s_split = s.split("-s1la2sh-").join("/");
    console.log(s_split);
    return AES.decrypt(s_split, key).toString(CryptoJS.enc.Utf8);
}
