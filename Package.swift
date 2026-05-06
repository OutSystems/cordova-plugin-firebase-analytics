// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "cordova-plugin-firebase-analytics",
    platforms: [.iOS(.v15)],
    products: [
        .library(
            name: "cordova-plugin-firebase-analytics",
            targets: ["cordova-plugin-firebase-analytics"])
    ],
    dependencies: [
        .package(url: "https://github.com/apache/cordova-ios.git", branch: "master"),
        .package(url: "https://github.com/firebase/firebase-ios-sdk.git", from: "10.29.0")
    ],
    targets: [
        .target(
            name: "cordova-plugin-firebase-analytics",
            dependencies: [
                .product(name: "Cordova", package: "cordova-ios"),
                .product(name: "FirebaseAnalytics", package: "firebase-ios-sdk"),
                "FirebaseAnalyticsPluginSwift"
            ],
            path: "src/ios",
            sources: ["FirebaseAnalyticsPlugin.m"],
            publicHeadersPath: "."),
        .target(
            name: "FirebaseAnalyticsPluginSwift",
            dependencies: [
                .product(name: "FirebaseAnalytics", package: "firebase-ios-sdk")
            ],
            path: "src/ios",
            sources: [
                "Common",
                "InputTransformer",
                "Manager",
                "Validator"
            ])
    ]
)
