-- because of new restriction for lint hint (it can't be empty)
-- we need to update all existing links without hint
update EXTERNAL_LINK set hint=title
where hint is null or length(trim(hint))=0;