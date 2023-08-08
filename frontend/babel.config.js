module.exports = function(api) {
  api.cache(true);
  return {
    presets: ['babel-preset-expo', "@babel/preset-env"],
    plugins: [
      require.resolve("expo-router/babel"),
      "@babel/plugin-transform-private-methods",
      "@babel/plugin-transform-private-property-in-object",
      "@babel/plugin-transform-class-properties",
    ],
  };
};
