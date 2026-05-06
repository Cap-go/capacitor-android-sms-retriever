import XCTest
@testable import AndroidSmsRetrieverPlugin

class AndroidSmsRetrieverTests: XCTestCase {
    func testGetPluginVersion() {
        let implementation = AndroidSmsRetriever()
        let result = implementation.getPluginVersion()

        XCTAssertEqual("8.0.0", result)
    }
}
