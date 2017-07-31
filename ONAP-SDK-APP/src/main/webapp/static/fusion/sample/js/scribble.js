    function storeUserScribble(id) {
      var scribble = document.getElementById('scribble').innerHTML;
      localStorage.setItem('userScribble',scribble);
    }

    function getUserScribble() {
      if ( localStorage.getItem('userScribble')) {
        var scribble = localStorage.getItem('userScribble');
      }
      else {
        var scribble = 'You can scribble directly on this sticky... and I will also remember your message the next time you visit my blog!';
      }
      document.getElementById('scribble').innerHTML = scribble;
    }

    function clearLocal() {
      clear: localStorage.clear(); 
      return false;
    }
