// Karma configuration for Angular 18
module.exports = function karmaConfig(config) {
  config.set({
    coverageReporter: {
      dir: require('node:path').join(__dirname, './coverage/angular-cli-project'),
      subdir: '.',
      reporters: [
        { type: 'html' },
        { type: 'lcovonly', file: 'lcov.info' },
        { type: 'text-summary' }
      ]
    }
  });
};
