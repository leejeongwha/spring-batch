package com.naver.batch.processor;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.naver.batch.model.BoardUser;

/**
 * CompositeItemProcessor 구현 시 여러개의 Processor가 동작하게 할 수 있음
 * @author nhn
 * @since 2015. 6. 19.
 */
@Component("boardUserProcessor")
public class BoardUserProcessor implements ItemProcessor<BoardUser, BoardUser> {
	private static final Logger logger = Logger.getLogger(BoardUserProcessor.class);

	public BoardUser process(BoardUser boardUser) throws Exception {
		logger.info(boardUser.toString());
		return boardUser;
	}

}
