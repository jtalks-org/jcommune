var AprilApril=function(a){if(typeof a==="object"){this.effects={};for(var b in a){if(a[b]&&this.effectTypes[b]){this.effects[b]=a[b]}}}else{if(a in this.effectTypes){this.effect=a}else{throw new Error("Effect name not recognized.")}}};var injectCSS=function(d,g,f){var a=document.body,e=document.createElement("style"),b=["webkitAnimation","MozAnimation","OAnimation","MSAnimation","animation"];e.type="text/css";e.appendChild(document.createTextNode(d));document.getElementsByTagName("head")[0].appendChild(e);for(var c=0;c<b.length;c++){if([b[c]+"Duration"] in a.style){a.style[b[c]+"Duration"]=g;a.style[b[c]+"FillMode"]="forwards";a.style[b[c]+"Name"]=f}}};AprilApril.prototype.effectTypes={scrambleText:function(){var a=1000,c=[].slice.call(document.querySelectorAll("p"));var b=function(){var f=c[Math.round(Math.random()*(c.length-1))],g=f.textContent.split(/\s/);if(!e(f)){f.textContent=d(g).join(" ")}};var d=function(i){var g=Math.round(Math.random()*(i.length-1)),h=Math.round(Math.random()*(i.length-1)),f=i[g];i[g]=i[h];i[h]=f;return i};var e=function(f){var g=f.getBoundingClientRect();return(g.top>=0&&g.left>=0&&g.bottom<=(window.innerHeight||document.documentElement.clientHeight)&&g.right<=(window.innerWidth||document.documentElement.clientWidth))};setInterval(b,a)},scrambleLinks:function(){var c=[].slice.call(document.links),f=[];var e=function(i){var g=Math.round((Math.random()*(i.length-1))),h=i[g];i.splice(g,1);return h};for(var d=0,a=c.length;d<a;d++){var b=c[d].href;if(b!==window.location.href+"#"&&b.match(/http/)){f.push(b)}}for(var d=0,a=c.length;d<a;d++){var b=c[d].href;if(b!==window.location.href+"#"&&b.match(/http/)){c[d].href=e(f)}}},shrink:function(){var b="3600s",a="@-webkit-keyframes shrink {100%{-webkit-transform: scale(.7);}}@-moz-keyframes shrink {100%{-moz-transform: scale(.7);}}@-o-keyframes shrink {100%{-o-transform: scale(.7);}}@keyframes shrink {100%{transform: scale(.7);}}";injectCSS(a,b,"shrink")},rotate:function(){var b="3600s",a="@-webkit-keyframes rotate {100%{-webkit-transform: rotateZ(90deg);}}@-moz-keyframes rotate {100%{-moz-transform: rotateZ(90deg);}}@-o-keyframes rotate {100%{-o-transform: rotateZ(90deg);}}@keyframes rotate {100%{transform: rotateZ(90deg);}}";injectCSS(a,b,"rotate")},replaceImages:function(){var d=document.querySelectorAll("img"),c=["http://placekitten.com/","http://www.fillmurray.com/","http://www.nicenicejpg.com/","http://www.placecage.com/","http://www.stevensegallery.com/"];for(var e=0,b=d.length;e<b;e++){var g=d[e],f=g.naturalWidth||g.clientWidth,a=g.naturalHeight||g.clientHeight;g.src=c[Math.round(Math.random()*(c.length-1))]+f+"/"+a}},sayPlease:function(){var c=[].slice.call(document.links),b=["Say please.","My goodness, where are your manners? Say please.","Just say please. I can do this all day.","You're really not going to say it, are you?","OK, now you're just testing how many of these there are.","This is the last one. Say please."],f=0;for(var d=0,a=c.length;d<a;d++){var e=c[d];e.addEventListener("click",function(h){h.preventDefault();var g=window.prompt(b[f]);if(g&&g.toLowerCase()==="please"){window.location=this.href}else{if(f<b.length-1){f++}else{f=0}}},false)}}};AprilApril.prototype.applyEffect=function(a){this.effectTypes[a].call()};AprilApril.prototype.fool=function(){if(this.effects){for(var a in this.effects){this.applyEffect(a)}}else{this.applyEffect(this.effect)}};