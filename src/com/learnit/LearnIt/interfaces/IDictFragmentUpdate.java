package com.learnit.LearnIt.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Created by igor on 4/2/14.
 */
public interface IDictFragmentUpdate {
	public void setQueryWordText(String word);
	public void setListEntries(List<Map<String,String>> words);
}
