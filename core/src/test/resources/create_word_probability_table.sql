CREATE TABLE IF NOT EXISTS word_probability (
    word VARCHAR(255),
    category VARCHAR(255),
    nonmatch_count INTEGER DEFAULT 0,
    match_count INTEGER DEFAULT 0,
    PRIMARY KEY(word, category)
);