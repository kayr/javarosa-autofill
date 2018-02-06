## Faker Expressions
```
address
    buildingNumber        : fake('address','buildingNumber')
    city                  : fake('address','city')
    cityName              : fake('address','cityName')
    cityPrefix            : fake('address','cityPrefix')
    citySuffix            : fake('address','citySuffix')
    country               : fake('address','country')
    countryCode           : fake('address','countryCode')
    firstName             : fake('address','firstName')
    fullAddress           : fake('address','fullAddress')
    lastName              : fake('address','lastName')
    latitude              : fake('address','latitude')
    longitude             : fake('address','longitude')
    secondaryAddress      : fake('address','secondaryAddress')
    state                 : fake('address','state')
    stateAbbr             : fake('address','stateAbbr')
    streetAddress         : fake('address','streetAddress')
    streetAddressNumber   : fake('address','streetAddressNumber')
    streetName            : fake('address','streetName')
    streetPrefix          : fake('address','streetPrefix')
    streetSuffix          : fake('address','streetSuffix')
    timeZone              : fake('address','timeZone')
    zipCode               : fake('address','zipCode')
----------------------------------------
ancient
    god                   : fake('ancient','god')
    hero                  : fake('ancient','hero')
    primordial            : fake('ancient','primordial')
    titan                 : fake('ancient','titan')
----------------------------------------
app
    author                : fake('app','author')
    name                  : fake('app','name')
    version               : fake('app','version')
----------------------------------------
artist
    name                  : fake('artist','name')
----------------------------------------
beer
    hop                   : fake('beer','hop')
    malt                  : fake('beer','malt')
    name                  : fake('beer','name')
    style                 : fake('beer','style')
    yeast                 : fake('beer','yeast')
----------------------------------------
book
    author                : fake('book','author')
    genre                 : fake('book','genre')
    publisher             : fake('book','publisher')
    title                 : fake('book','title')
----------------------------------------
bool
    bool                  : fake('bool','bool')
----------------------------------------
business
    creditCardExpiry      : fake('business','creditCardExpiry')
    creditCardNumber      : fake('business','creditCardNumber')
    creditCardType        : fake('business','creditCardType')
----------------------------------------
cat
    breed                 : fake('cat','breed')
    name                  : fake('cat','name')
    registry              : fake('cat','registry')
----------------------------------------
chuckNorris
    fact                  : fake('chuckNorris','fact')
----------------------------------------
code
    asin                  : fake('code','asin')
    ean13                 : fake('code','ean13')
    ean8                  : fake('code','ean8')
    gtin13                : fake('code','gtin13')
    gtin8                 : fake('code','gtin8')
    imei                  : fake('code','imei')
    isbn10                : fake('code','isbn10')
    isbn13                : fake('code','isbn13')
    isbnGroup             : fake('code','isbnGroup')
    isbnGs1               : fake('code','isbnGs1')
    isbnRegistrant        : fake('code','isbnRegistrant')
----------------------------------------
color
    name                  : fake('color','name')
----------------------------------------
commerce
    color                 : fake('commerce','color')
    department            : fake('commerce','department')
    material              : fake('commerce','material')
    price                 : fake('commerce','price')
    productName           : fake('commerce','productName')
    promotionCode         : fake('commerce','promotionCode')
----------------------------------------
company
    bs                    : fake('company','bs')
    buzzword              : fake('company','buzzword')
    catchPhrase           : fake('company','catchPhrase')
    industry              : fake('company','industry')
    logo                  : fake('company','logo')
    name                  : fake('company','name')
    profession            : fake('company','profession')
    suffix                : fake('company','suffix')
    url                   : fake('company','url')
----------------------------------------
crypto
    md5                   : fake('crypto','md5')
    sha1                  : fake('crypto','sha1')
    sha256                : fake('crypto','sha256')
    sha512                : fake('crypto','sha512')
----------------------------------------
date
    birthday              : fake('date','birthday')
----------------------------------------
demographic
    demonym               : fake('demographic','demonym')
    educationalAttainment : fake('demographic','educationalAttainment')
    maritalStatus         : fake('demographic','maritalStatus')
    race                  : fake('demographic','race')
    sex                   : fake('demographic','sex')
----------------------------------------
educator
    campus                : fake('educator','campus')
    course                : fake('educator','course')
    secondarySchool       : fake('educator','secondarySchool')
    university            : fake('educator','university')
----------------------------------------
esports
    event                 : fake('esports','event')
    game                  : fake('esports','game')
    league                : fake('esports','league')
    player                : fake('esports','player')
    team                  : fake('esports','team')
----------------------------------------
file
    extension             : fake('file','extension')
    fileName              : fake('file','fileName')
    mimeType              : fake('file','mimeType')
----------------------------------------
finance
    bic                   : fake('finance','bic')
    creditCard            : fake('finance','creditCard')
    iban                  : fake('finance','iban')
----------------------------------------
food
    ingredient            : fake('food','ingredient')
    measurement           : fake('food','measurement')
    spice                 : fake('food','spice')
----------------------------------------
friends
    character             : fake('friends','character')
    location              : fake('friends','location')
    quote                 : fake('friends','quote')
----------------------------------------
gameOfThrones
    character             : fake('gameOfThrones','character')
    city                  : fake('gameOfThrones','city')
    dragon                : fake('gameOfThrones','dragon')
    house                 : fake('gameOfThrones','house')
    quote                 : fake('gameOfThrones','quote')
----------------------------------------
hacker
    abbreviation          : fake('hacker','abbreviation')
    adjective             : fake('hacker','adjective')
    ingverb               : fake('hacker','ingverb')
    noun                  : fake('hacker','noun')
    verb                  : fake('hacker','verb')
----------------------------------------
harryPotter
    book                  : fake('harryPotter','book')
    character             : fake('harryPotter','character')
    location              : fake('harryPotter','location')
    quote                 : fake('harryPotter','quote')
----------------------------------------
hipster
    word                  : fake('hipster','word')
----------------------------------------
idNumber
    invalid               : fake('idNumber','invalid')
    invalidSvSeSsn        : fake('idNumber','invalidSvSeSsn')
    ssnValid              : fake('idNumber','ssnValid')
    valid                 : fake('idNumber','valid')
    validSvSeSsn          : fake('idNumber','validSvSeSsn')
----------------------------------------
internet
    avatar                : fake('internet','avatar')
    domainName            : fake('internet','domainName')
    domainSuffix          : fake('internet','domainSuffix')
    domainWord            : fake('internet','domainWord')
    emailAddress          : fake('internet','emailAddress')
    image                 : fake('internet','image')
    ipV4Address           : fake('internet','ipV4Address')
    ipV4Cidr              : fake('internet','ipV4Cidr')
    ipV6Address           : fake('internet','ipV6Address')
    ipV6Cidr              : fake('internet','ipV6Cidr')
    macAddress            : fake('internet','macAddress')
    password              : fake('internet','password')
    privateIpV4Address    : fake('internet','privateIpV4Address')
    publicIpV4Address     : fake('internet','publicIpV4Address')
    safeEmailAddress      : fake('internet','safeEmailAddress')
    slug                  : fake('internet','slug')
    url                   : fake('internet','url')
----------------------------------------
job
    field                 : fake('job','field')
    keySkills             : fake('job','keySkills')
    position              : fake('job','position')
    seniority             : fake('job','seniority')
    title                 : fake('job','title')
----------------------------------------
lordOfTheRings
    character             : fake('lordOfTheRings','character')
    location              : fake('lordOfTheRings','location')
----------------------------------------
lorem
    character             : fake('lorem','character')
    characters            : fake('lorem','characters')
    paragraph             : fake('lorem','paragraph')
    sentence              : fake('lorem','sentence')
    word                  : fake('lorem','word')
    words                 : fake('lorem','words')
----------------------------------------
matz
    quote                 : fake('matz','quote')
----------------------------------------
music
    chord                 : fake('music','chord')
    instrument            : fake('music','instrument')
    key                   : fake('music','key')
----------------------------------------
name
    firstName             : fake('name','firstName')
    fullName              : fake('name','fullName')
    lastName              : fake('name','lastName')
    name                  : fake('name','name')
    nameWithMiddle        : fake('name','nameWithMiddle')
    prefix                : fake('name','prefix')
    suffix                : fake('name','suffix')
    title                 : fake('name','title')
    username              : fake('name','username')
----------------------------------------
number
    digit                 : fake('number','digit')
    randomDigit           : fake('number','randomDigit')
    randomDigitNotZero    : fake('number','randomDigitNotZero')
    randomNumber          : fake('number','randomNumber')
----------------------------------------
options
----------------------------------------
phoneNumber
    cellPhone             : fake('phoneNumber','cellPhone')
    phoneNumber           : fake('phoneNumber','phoneNumber')
----------------------------------------
pokemon
    location              : fake('pokemon','location')
    name                  : fake('pokemon','name')
----------------------------------------
random
    nextBoolean           : fake('random','nextBoolean')
    nextDouble            : fake('random','nextDouble')
    nextLong              : fake('random','nextLong')
----------------------------------------
rickAndMorty
    character             : fake('rickAndMorty','character')
    location              : fake('rickAndMorty','location')
    quote                 : fake('rickAndMorty','quote')
----------------------------------------
rockBand
    name                  : fake('rockBand','name')
----------------------------------------
shakespeare
    asYouLikeItQuote      : fake('shakespeare','asYouLikeItQuote')
    hamletQuote           : fake('shakespeare','hamletQuote')
    kingRichardIIIQuote   : fake('shakespeare','kingRichardIIIQuote')
    romeoAndJulietQuote   : fake('shakespeare','romeoAndJulietQuote')
----------------------------------------
slackEmoji
    activity              : fake('slackEmoji','activity')
    celebration           : fake('slackEmoji','celebration')
    custom                : fake('slackEmoji','custom')
    emoji                 : fake('slackEmoji','emoji')
    foodAndDrink          : fake('slackEmoji','foodAndDrink')
    nature                : fake('slackEmoji','nature')
    objectsAndSymbols     : fake('slackEmoji','objectsAndSymbols')
    people                : fake('slackEmoji','people')
    travelAndPlaces       : fake('slackEmoji','travelAndPlaces')
----------------------------------------
space
    agency                : fake('space','agency')
    agencyAbbreviation    : fake('space','agencyAbbreviation')
    company               : fake('space','company')
    constellation         : fake('space','constellation')
    distanceMeasurement   : fake('space','distanceMeasurement')
    galaxy                : fake('space','galaxy')
    meteorite             : fake('space','meteorite')
    moon                  : fake('space','moon')
    nasaSpaceCraft        : fake('space','nasaSpaceCraft')
    nebula                : fake('space','nebula')
    planet                : fake('space','planet')
    star                  : fake('space','star')
    starCluster           : fake('space','starCluster')
----------------------------------------
stock
    nsdqSymbol            : fake('stock','nsdqSymbol')
    nyseSymbol            : fake('stock','nyseSymbol')
----------------------------------------
superhero
    descriptor            : fake('superhero','descriptor')
    name                  : fake('superhero','name')
    power                 : fake('superhero','power')
    prefix                : fake('superhero','prefix')
    suffix                : fake('superhero','suffix')
----------------------------------------
team
    creature              : fake('team','creature')
    name                  : fake('team','name')
    sport                 : fake('team','sport')
    state                 : fake('team','state')
----------------------------------------
twinPeaks
    character             : fake('twinPeaks','character')
    location              : fake('twinPeaks','location')
    quote                 : fake('twinPeaks','quote')
----------------------------------------
university
    name                  : fake('university','name')
    prefix                : fake('university','prefix')
    suffix                : fake('university','suffix')
----------------------------------------
witcher
    character             : fake('witcher','character')
    location              : fake('witcher','location')
    monster               : fake('witcher','monster')
    quote                 : fake('witcher','quote')
    school                : fake('witcher','school')
    witcher               : fake('witcher','witcher')
----------------------------------------
yoda
    quote                 : fake('yoda','quote')
----------------------------------------
zelda
    character             : fake('zelda','character')
    game                  : fake('zelda','game')
----------------------------------------
```

## Other Fakers
```
random-date-between('2012-01-01','2013-01-01')
random-date-between(now(),'2090-01-01')

random-future-date(5,'days') 
random-future-date(5,'days','2020-1-1') -> a random date 5 days after '2020-1-1'
random-future-date(5,'seconds','12:00')
random-future-date(5,'minutes')
random-future-date(5,'hour')

random-past-date(5,'days') 
random-past-date(5,'days','2020-1-1') -> a random date 5 days before '2020-1-1'
random-past-date(5,'seconds','12:00')
random-past-date(5,'minutes')
random-past-date(5,'hour')

random-regex('077[0-9]{10}') -> generate a number that starts with 077 and ends with 10 random digits

random-number(1000000000) -> produces an answer > 1_000_000_000
random-number(2.0,7.0)  -> A number between 2.0 and 7.0
random-number(2,1,2) -> a number between 1 and 2 with 2 decimal places
random-number()

```

## Support functions to ease generation of test data
 Coming soon 
