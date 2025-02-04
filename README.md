# FamilyCart

FamilyCart is a user-friendly Android app designed to simplify shopping for couples and families. It allows users to collaboratively manage shopping lists, track expenses, and stay within their monthly budget.

## Features

- **Collaborative Shopping List**: Both you and your partner can add items to the shared shopping list.
- **Mark Items as Completed**: Check off items as you purchase them, ensuring you don't miss anything.
- **Price Tracking**: Set prices for individual items to keep track of expenses.
- **Expense Calculation**: Automatically calculate the total expense of your shopping list to help manage your budget effectively.

## Firebase Integration

FamilyCart uses Firebase as its database. This allows each couple to use the app personally by configuring their own Firebase project. Your data remains private and accessible only to you and your partner.

## Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/AkibHossainOmi/familycart.git
   ```
2. **Configure Firebase**
   - Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/).
   - Download the `google-services.json` file for Android and place it in the `app/` directory.
   - Enable the required Firebase services (e.g., Firestore Database, Authentication).
3. **Open in Android Studio**
   - Launch Android Studio.
   - Open the project directory.
4. **Build and Run**
   - Connect your Android device or start an emulator.
   - Click on the "Run" button to install and launch the app.

## Usage

1. **Add Items**: Tap the "Add" button to add items to your shopping list. Enter the item name and optionally its price.
2. **Mark as Completed**: Tap on an item to mark it as purchased.
3. **Calculate Total**: View the total expense at the bottom of the list to track your spending.

<!-- ## Screenshots

(Add screenshots of the app interface here.) -->

## Technologies Used

- **Android Development**: Built using Kotlin/Java.
- **Firebase**: Firestore Database for real-time data synchronization.
- **UI/UX**: Designed for simplicity and ease of use.

## Note

This app was lovingly created for my wife, the one who inspires me every day. ❤️

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## Feedback and Contributions

If you have any feedback, suggestions, or issues, feel free to open an issue on the GitHub repository or reach out at [akibhossainomi2000@gmail.com].

---

Happy Shopping with FamilyCart!

