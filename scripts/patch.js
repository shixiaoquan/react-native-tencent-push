const patch = require('mpatch');
const recipes = require('./recipes');

Object.keys(recipes).forEach(file => {
  patch(file, [].concat(recipes[file]))
})