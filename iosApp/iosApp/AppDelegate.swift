import UIKit
import FirebaseCore
import FirebaseMessaging
import shared

class AppDelegate: NSObject, UIApplicationDelegate {
    private let fcmDelegate = IosFcmDelegate()

    func application(_ application: UIApplication,
                   didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

        FirebaseApp.configure()
        UNUserNotificationCenter.current().delegate = self
        application.registerForRemoteNotifications()
        SharedModuleKt.setupFcmDelegate(delegate: fcmDelegate)

        return true
    }

    func application(_ application: UIApplication,
                   didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }
}

extension AppDelegate: UNUserNotificationCenterDelegate {
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                              willPresent notification: UNNotification,
                              withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.banner, .sound])
    }

    func userNotificationCenter(_ center: UNUserNotificationCenter,
                              didReceive response: UNNotificationResponse,
                              withCompletionHandler completionHandler: @escaping () -> Void) {
        completionHandler()
    }
}