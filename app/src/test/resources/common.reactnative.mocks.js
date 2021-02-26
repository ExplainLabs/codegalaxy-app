
const scommonsMocks = require("./sc-react-native-mocks.js")

module.exports = {
  ...scommonsMocks,
  Linking: {
    openURL: function (url) {
      return Promise.resolve(undefined)
    }
  }
}
