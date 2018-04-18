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

$(function () {
  'use strict';

  QUnit.module('affix plugin')

  QUnit.test('should be defined on jquery object', function (assert) {
    assert.expect(1)
    assert.ok($(document.body).affix, 'affix method is defined')
  })

  QUnit.module('affix', {
    beforeEach: function () {
      // Run all tests in noConflict mode -- it's the only way to ensure that the plugin works in noConflict mode
      $.fn.bootstrapAffix = $.fn.affix.noConflict()
    },
    afterEach: function () {
      $.fn.affix = $.fn.bootstrapAffix
      delete $.fn.bootstrapAffix
    }
  })

  QUnit.test('should provide no conflict', function (assert) {
    assert.expect(1)
    assert.strictEqual($.fn.affix, undefined, 'affix was set back to undefined (org value)')
  })

  QUnit.test('should return jquery collection containing the element', function (assert) {
    assert.expect(2)
    var $el = $('<div/>')
    var $affix = $el.bootstrapAffix()
    assert.ok($affix instanceof $, 'returns jquery collection')
    assert.strictEqual($affix[0], $el[0], 'collection contains element')
  })

  QUnit.test('should exit early if element is not visible', function (assert) {
    assert.expect(1)
    var $affix = $('<div style="display: none"/>').bootstrapAffix()
    $affix.data('bs.affix').checkPosition()
    assert.ok(!$affix.hasClass('affix'), 'affix class was not added')
  })

  QUnit.test('should trigger affixed event after affix', function (assert) {
    assert.expect(2)
    var done = assert.async()

    var templateHTML = '<div id="affixTarget">'
        + '<ul>'
        + '<li>Please affix</li>'
        + '<li>And unaffix</li>'
        + '</ul>'
        + '</div>'
        + '<div id="affixAfter" style="height: 20000px; display: block;"/>'
    $(templateHTML).appendTo(document.body)

    $('#affixTarget').bootstrapAffix({
      offset: $('#affixTarget ul').position()
    })

    $('#affixTarget')
      .on('affix.bs.affix', function () {
        assert.ok(true, 'affix event fired')
      }).on('affixed.bs.affix', function () {
        assert.ok(true, 'affixed event fired')
        $('#affixTarget, #affixAfter').remove()
        done()
      })

    setTimeout(function () {
      window.scrollTo(0, document.body.scrollHeight)

      setTimeout(function () {
        window.scroll(0, 0)
      }, 16) // for testing in a browser
    }, 0)
  })

  QUnit.test('should affix-top when scrolling up to offset when parent has padding', function (assert) {
    assert.expect(1)
    var done = assert.async()

    var templateHTML = '<div id="padding-offset" style="padding-top: 20px;">'
        + '<div id="affixTopTarget">'
        + '<p>Testing affix-top class is added</p>'
        + '</div>'
        + '<div style="height: 1000px; display: block;"/>'
        + '</div>'
    $(templateHTML).appendTo(document.body)

    $('#affixTopTarget')
      .bootstrapAffix({
        offset: { top: 120, bottom: 0 }
      })
      .on('affixed-top.bs.affix', function () {
        assert.ok($('#affixTopTarget').hasClass('affix-top'), 'affix-top class applied')
        $('#padding-offset').remove()
        done()
      })

    setTimeout(function () {
      window.scrollTo(0, document.body.scrollHeight)

      setTimeout(function () {
        window.scroll(0, 119)
      }, 250)
    }, 250)
  })
})
