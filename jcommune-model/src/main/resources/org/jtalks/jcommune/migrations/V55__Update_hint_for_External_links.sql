update EXTERNAL_LINK set hint=title
where hint is null or length(trim(hint))=0;