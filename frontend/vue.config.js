module.exports = {
  publicPath: "./",
  outputDir: "dist",

  lintOnSave: false,
  devServer: {
    host: "localhost",
    port: 21091,
    https: false,
    // webpack-dev-server v4 (Vue CLI 5) no longer accepts a string here
    proxy: {
      "/api": {
        target: "http://localhost:21090",
        changeOrigin: true,
      },
    },
    client: {
      overlay: {
        warnings: false,
        errors: false,
      },
    },
  },
};
