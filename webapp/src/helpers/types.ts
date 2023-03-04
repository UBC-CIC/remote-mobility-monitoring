export const userTypes: strObjMap = {
    "caregiver": "car-",
    "patient": "pat-",
    "admin": "adm-",
};

export interface strObjMap {
    [key: string]: string
}

export const userTypesLength = 4;

export const getCaregiverId = () => {
    const sub = localStorage.getItem("sub");
    if (! sub) {
        return "";
    }
    if (!sub.startsWith(userTypes["caregiver"])) {
        return "";
    }
    return sub;
};

