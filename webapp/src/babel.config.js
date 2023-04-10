module.exports = {
  "presets": [
    '@babel/preset-env', 
    '@babel/preset-react', 
    {targets: {node: 'current'}},
    '@babel/preset-typescript'
  ],
  
  "plugins": [
    // Add any additional Babel plugins you want to use here
    '@babel/plugin-syntax-jsx'
  ]
};