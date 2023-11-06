package com.ssafy.house.filter;

import org.apache.commons.lang.StringEscapeUtils;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;

import nonapi.io.github.classgraph.utils.StringUtils;

public class HTMLCharacterExcapes extends CharacterEscapes{
	
	private static final long serialVersionID=1L;
	private final int[] asciiEscapes;
	
	public HTMLCharacterExcapes() {
		// XSS 방지 처리할 특수 문자 지정
		asciiEscapes=CharacterEscapes.standardAsciiEscapesForJSON();
		asciiEscapes['<']=CharacterEscapes.ESCAPE_CUSTOM;
		asciiEscapes['>']=CharacterEscapes.ESCAPE_CUSTOM;
		asciiEscapes['&']=CharacterEscapes.ESCAPE_CUSTOM;
		asciiEscapes['\"']=CharacterEscapes.ESCAPE_CUSTOM;
		asciiEscapes['(']=CharacterEscapes.ESCAPE_CUSTOM;
		asciiEscapes[')']=CharacterEscapes.ESCAPE_CUSTOM;
		asciiEscapes['#']=CharacterEscapes.ESCAPE_CUSTOM;
		asciiEscapes['\'']=CharacterEscapes.ESCAPE_CUSTOM;
	}
	@Override
	public int[] getEscapeCodesForAscii() {
		return asciiEscapes;
	}

	@Override
	public SerializableString getEscapeSequence(int ch) {
		return new SerializedString(StringEscapeUtils.escapeHtml(Character.toString((char) ch)));
	}

}
