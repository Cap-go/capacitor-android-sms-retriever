import Foundation
import Capacitor

@objc(AndroidSmsRetrieverPlugin)
public class AndroidSmsRetrieverPlugin: CAPPlugin, CAPBridgedPlugin {
    private let pluginVersion = "8.0.0"
    public let identifier = "AndroidSmsRetrieverPlugin"
    public let jsName = "AndroidSmsRetriever"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "startWatch", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "stopWatch", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getHashString", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getPhoneNumber", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getPluginVersion", returnType: CAPPluginReturnPromise)
    ]

    @objc func startWatch(_ call: CAPPluginCall) {
        call.reject("startWatch is only available on Android.")
    }

    @objc func stopWatch(_ call: CAPPluginCall) {
        call.reject("stopWatch is only available on Android.")
    }

    @objc func getHashString(_ call: CAPPluginCall) {
        call.reject("getHashString is only available on Android.")
    }

    @objc func getPhoneNumber(_ call: CAPPluginCall) {
        call.reject("getPhoneNumber is only available on Android.")
    }

    @objc func getPluginVersion(_ call: CAPPluginCall) {
        call.resolve(["version": self.pluginVersion])
    }
}
