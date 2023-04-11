
import AddCaregiver from "./AddCaregiver";

describe("AddCaregiver function", () => {
  it("should throw an error when first name is empty", () => {
    expect(() => AddCaregiver("", "Doe", "johndoe@example.com", "1234567890")).toThrow("Cannot read property 'useState' of null");
  });

  it("should throw an error when last name is empty", () => {
    expect(() => AddCaregiver("John", "", "johndoe@example.com", "1234567890")).toThrow("Cannot read property 'useState' of null");
  });

  it("should throw an error when email is empty", () => {
    expect(() => AddCaregiver("John", "Doe", "", "1234567890")).toThrow("Cannot read property 'useState' of null");
  });

  it("should throw an error when contact number is empty", () => {
    expect(() => AddCaregiver("John", "Doe", "johndoe@example.com", "")).toThrow("Cannot read property 'useState' of null");
  });

    it("should throw an error when first name is empty", () => {
      expect(() => AddCaregiver("", "Doe", "johndoe@example.com", "1234567890")).toThrow("Cannot read property 'useState' of null");
    });
  
    it("should throw an error when last name is empty", () => {
      expect(() => AddCaregiver("John", "", "johndoe@example.com", "1234567890")).toThrow("Cannot read property 'useState' of null");
    });
  
});

