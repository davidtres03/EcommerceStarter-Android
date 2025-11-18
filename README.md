# EcommerceStarter Android Admin App

Official Android admin panel for [EcommerceStarter](https://github.com/davidtres03/EcommerceStarter) - the open-source e-commerce platform.

## 🎯 Overview

EcommerceStarter Admin is a native Android application that allows store owners and managers to manage their EcommerceStarter installation from anywhere. Built with modern Android development practices using Kotlin and Jetpack Compose.

## ✨ Features

### Current (v1.0.0 - In Development)
- 🔐 **Secure Authentication** - JWT-based login
- 📊 **Dashboard** - Real-time store metrics and analytics
- 📦 **Order Management** - View, process, and update orders
- 🛍️ **Product Management** - Add, edit, delete products and inventory
- 👥 **Customer Management** - View customer information and history
- ⚙️ **Configuration** - Manage store settings and API keys
- 🔔 **Push Notifications** - Real-time alerts for new orders and events
- 📱 **Offline Support** - Cache data for offline access

### Planned Features
- 📸 **Barcode Scanning** - Quick product lookup and inventory updates
- 🎤 **Voice Commands** - Hands-free operation
- 📈 **Advanced Analytics** - Detailed sales and performance reports
- 🔄 **Self-Updating** - In-app upgrade manager

## 🏗️ Architecture

Built following Clean Architecture principles:

```
├── data/          # Data sources, API clients, repositories
├── domain/        # Business logic, use cases, models
├── presentation/  # UI layer with Jetpack Compose
└── di/            # Dependency injection modules
```

**Tech Stack:**
- **Language:** Kotlin 2.0.21
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** Clean Architecture + MVVM
- **Dependency Injection:** Hilt (Dagger)
- **Networking:** Retrofit + OkHttp
- **Local Database:** Room
- **Async:** Coroutines + Flow
- **Image Loading:** Coil
- **Push Notifications:** Firebase Cloud Messaging

## 📋 Requirements

- **Android 7.0 (API 24)** or higher
- **EcommerceStarter v1.0+** server installation
- Admin account credentials

## 🚀 Installation

### For Users

1. Download from [Google Play Store](#) *(Coming Soon)*
2. Launch app and enter your EcommerceStarter server URL
3. Login with your admin credentials
4. Start managing your store!

### For Developers

```bash
# Clone the repository
git clone https://github.com/davidtres03/EcommerceStarter-Android.git
cd EcommerceStarter-Android

# Open in Android Studio
# File → Open → Select EcommerceStarter-Android folder

# Build and run
# Click the green play button or press Shift+F10
```

## 🔧 Configuration

On first launch, you'll be prompted to configure your server:

```
Server URL: https://your-store.com
API Version: v1 (default)
```

The app supports multiple server configurations, allowing you to manage different stores from one device.

## 🧪 Development Setup

1. **Prerequisites:**
   - Android Studio Ladybug (2024.2.1) or later
   - JDK 17 or 21
   - Android SDK (API 24-35)

2. **Configure Firebase:**
   - Add your `google-services.json` to `app/`
   - Update Firebase configuration for your project

3. **Update API Endpoint:**
   - Edit `NetworkModule.kt`
   - Set `BASE_URL` to your development server

4. **Build:**
   ```bash
   ./gradlew assembleDebug
   ```

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## 📦 Building for Release

See [RELEASE.md](RELEASE.md) for detailed release instructions.

## 🤝 Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### Development Process

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

## 🔗 Related Projects

- **[EcommerceStarter](https://github.com/davidtres03/EcommerceStarter)** - The main backend platform (ASP.NET Core)
- **EcommerceStarter Shop** *(Coming Soon)* - Customer-facing shopping app

## 📞 Support

- **Issues:** [GitHub Issues](https://github.com/davidtres03/EcommerceStarter-Android/issues)
- **Discussions:** [GitHub Discussions](https://github.com/davidtres03/EcommerceStarter-Android/discussions)
- **Documentation:** [Wiki](https://github.com/davidtres03/EcommerceStarter-Android/wiki)

## 🎯 Roadmap

- [x] Project setup and architecture
- [x] Authentication system
- [ ] Dashboard implementation
- [ ] Order management
- [ ] Product management
- [ ] Settings and configuration
- [ ] Push notifications
- [ ] Image upload from camera
- [ ] Barcode scanning
- [ ] Google Play Store release
- [ ] Customer shopping app (separate project)

## 🙏 Acknowledgments

Built with ❤️ by [Cap and Collar Supply Co](https://github.com/davidtres03)

Powered by:
- [Android Jetpack](https://developer.android.com/jetpack)
- [Kotlin](https://kotlinlang.org/)
- [Material Design 3](https://m3.material.io/)

---

**Star ⭐ this repo if you find it helpful!**
