/*
 * ============LICENSE_START==================================================
 * ONAP Policy Engine
 * ===========================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ===========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END====================================================
 */

'use strict';
var fs = require('fs');
var path = require('path');

var COMMONJS_BANNER = '// This file is autogenerated via the `commonjs` Grunt task. You can require() this file in a CommonJS environment.\n';

module.exports = function generateCommonJSModule(grunt, srcFiles, destFilepath) {
  var destDir = path.dirname(destFilepath);

  function srcPathToDestRequire(srcFilepath) {
    var requirePath = path.relative(destDir, srcFilepath).replace(/\\/g, '/');
    return 'require(\'' + requirePath + '\')';
  }

  var moduleOutputJs = COMMONJS_BANNER + srcFiles.map(srcPathToDestRequire).join('\n');
  try {
    fs.writeFileSync(destFilepath, moduleOutputJs);
  }
  catch (err) {
    grunt.fail.warn(err);
  }
  grunt.log.writeln('File ' + destFilepath.cyan + ' created.');
};
