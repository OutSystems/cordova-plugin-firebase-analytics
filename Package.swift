// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "cordova-plugin-firebase-analytics",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "cordova-plugin-firebase-analytics",
            targets: ["FirebaseAnalyticsPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/apache/cordova-ios.git", branch: "master"),
        .package(url: "https://github.com/firebase/firebase-ios-sdk.git", from: "10.29.0")
    ],
    targets: [
        .target(
            name: "FirebaseAnalyticsPlugin",
            dependencies: [
                .product(name: "Cordova", package: "cordova-ios"),
                .product(name: "FirebaseAnalytics", package: "firebase-ios-sdk")
            ],
            path: "src/ios")
    ]
)
