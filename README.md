## Faker Expressions
```
name
    name                  : fake('name','name')
    prefix                : fake('name','prefix')
    title                 : fake('name','title')
    suffix                : fake('name','suffix')
    nameWithMiddle        : fake('name','nameWithMiddle')
    lastName              : fake('name','lastName')
    firstName             : fake('name','firstName')
    fullName              : fake('name','fullName')
    username              : fake('name','username')
----------------------------------------
file
    fileName              : fake('file','fileName')
    extension             : fake('file','extension')
    mimeType              : fake('file','mimeType')
----------------------------------------
address
    state                 : fake('address','state')
    country               : fake('address','country')
    timeZone              : fake('address','timeZone')
    streetAddressNumber   : fake('address','streetAddressNumber')
    streetName            : fake('address','streetName')
    streetAddress         : fake('address','streetAddress')
    secondaryAddress      : fake('address','secondaryAddress')
    lastName              : fake('address','lastName')
    firstName             : fake('address','firstName')
    longitude             : fake('address','longitude')
    countryCode           : fake('address','countryCode')
    buildingNumber        : fake('address','buildingNumber')
    streetPrefix          : fake('address','streetPrefix')
    latitude              : fake('address','latitude')
    stateAbbr             : fake('address','stateAbbr')
    cityPrefix            : fake('address','cityPrefix')
    fullAddress           : fake('address','fullAddress')
    streetSuffix          : fake('address','streetSuffix')
    city                  : fake('address','city')
    citySuffix            : fake('address','citySuffix')
    zipCode               : fake('address','zipCode')
    cityName              : fake('address','cityName')
----------------------------------------
random
    nextDouble            : fake('random','nextDouble')
    nextLong              : fake('random','nextLong')
    nextBoolean           : fake('random','nextBoolean')
----------------------------------------
number
    digit                 : fake('number','digit')
    randomDigitNotZero    : fake('number','randomDigitNotZero')
    randomDigit           : fake('number','randomDigit')
    randomNumber          : fake('number','randomNumber')
----------------------------------------
date
----------------------------------------
color
    name                  : fake('color','name')
----------------------------------------
code
    asin                  : fake('code','asin')
    imei                  : fake('code','imei')
    ean8                  : fake('code','ean8')
    gtin8                 : fake('code','gtin8')
    isbnGs1               : fake('code','isbnGs1')
    ean13                 : fake('code','ean13')
    gtin13                : fake('code','gtin13')
    isbn10                : fake('code','isbn10')
    isbn13                : fake('code','isbn13')
    isbnGroup             : fake('code','isbnGroup')
    isbnRegistrant        : fake('code','isbnRegistrant')
----------------------------------------
options
----------------------------------------
bool
    bool                  : fake('bool','bool')
----------------------------------------
phoneNumber
    phoneNumber           : fake('phoneNumber','phoneNumber')
    cellPhone             : fake('phoneNumber','cellPhone')
----------------------------------------
pokemon
    name                  : fake('pokemon','name')
    location              : fake('pokemon','location')
----------------------------------------
company
    name                  : fake('company','name')
    url                   : fake('company','url')
    suffix                : fake('company','suffix')
    logo                  : fake('company','logo')
    bs                    : fake('company','bs')
    catchPhrase           : fake('company','catchPhrase')
    buzzword              : fake('company','buzzword')
    industry              : fake('company','industry')
    profession            : fake('company','profession')
----------------------------------------
crypto
    sha512                : fake('crypto','sha512')
    md5                   : fake('crypto','md5')
    sha1                  : fake('crypto','sha1')
    sha256                : fake('crypto','sha256')
----------------------------------------
idNumber
    valid                 : fake('idNumber','valid')
    validSvSeSsn          : fake('idNumber','validSvSeSsn')
    ssnValid              : fake('idNumber','ssnValid')
    invalidSvSeSsn        : fake('idNumber','invalidSvSeSsn')
    invalid               : fake('idNumber','invalid')
----------------------------------------
app
    name                  : fake('app','name')
    version               : fake('app','version')
    author                : fake('app','author')
----------------------------------------
book
    title                 : fake('book','title')
    author                : fake('book','author')
    publisher             : fake('book','publisher')
    genre                 : fake('book','genre')
----------------------------------------
artist
    name                  : fake('artist','name')
----------------------------------------
internet
    url                   : fake('internet','url')
    image                 : fake('internet','image')
    publicIpV4Address     : fake('internet','publicIpV4Address')
    privateIpV4Address    : fake('internet','privateIpV4Address')
    ipV4Address           : fake('internet','ipV4Address')
    avatar                : fake('internet','avatar')
    emailAddress          : fake('internet','emailAddress')
    domainWord            : fake('internet','domainWord')
    safeEmailAddress      : fake('internet','safeEmailAddress')
    ipV6Address           : fake('internet','ipV6Address')
    ipV6Cidr              : fake('internet','ipV6Cidr')
    domainName            : fake('internet','domainName')
    password              : fake('internet','password')
    macAddress            : fake('internet','macAddress')
    slug                  : fake('internet','slug')
    domainSuffix          : fake('internet','domainSuffix')
    ipV4Cidr              : fake('internet','ipV4Cidr')
----------------------------------------
business
    creditCardNumber      : fake('business','creditCardNumber')
    creditCardExpiry      : fake('business','creditCardExpiry')
    creditCardType        : fake('business','creditCardType')
----------------------------------------
hacker
    verb                  : fake('hacker','verb')
    ingverb               : fake('hacker','ingverb')
    adjective             : fake('hacker','adjective')
    abbreviation          : fake('hacker','abbreviation')
    noun                  : fake('hacker','noun')
----------------------------------------
finance
    bic                   : fake('finance','bic')
    creditCard            : fake('finance','creditCard')
    iban                  : fake('finance','iban')
----------------------------------------
food
    measurement           : fake('food','measurement')
    ingredient            : fake('food','ingredient')
    spice                 : fake('food','spice')
----------------------------------------
ancient
    primordial            : fake('ancient','primordial')
    titan                 : fake('ancient','titan')
    god                   : fake('ancient','god')
    hero                  : fake('ancient','hero')
----------------------------------------
chuckNorris
    fact                  : fake('chuckNorris','fact')
----------------------------------------
music
    key                   : fake('music','key')
    instrument            : fake('music','instrument')
    chord                 : fake('music','chord')
----------------------------------------
gameOfThrones
    quote                 : fake('gameOfThrones','quote')
    character             : fake('gameOfThrones','character')
    house                 : fake('gameOfThrones','house')
    dragon                : fake('gameOfThrones','dragon')
    city                  : fake('gameOfThrones','city')
----------------------------------------
lorem
    words                 : fake('lorem','words')
    characters            : fake('lorem','characters')
    word                  : fake('lorem','word')
    sentence              : fake('lorem','sentence')
    paragraph             : fake('lorem','paragraph')
    character             : fake('lorem','character')
----------------------------------------
commerce
    color                 : fake('commerce','color')
    productName           : fake('commerce','productName')
    department            : fake('commerce','department')
    price                 : fake('commerce','price')
    promotionCode         : fake('commerce','promotionCode')
    material              : fake('commerce','material')
----------------------------------------
slackEmoji
    objectsAndSymbols     : fake('slackEmoji','objectsAndSymbols')
    celebration           : fake('slackEmoji','celebration')
    custom                : fake('slackEmoji','custom')
    emoji                 : fake('slackEmoji','emoji')
    people                : fake('slackEmoji','people')
    foodAndDrink          : fake('slackEmoji','foodAndDrink')
    travelAndPlaces       : fake('slackEmoji','travelAndPlaces')
    nature                : fake('slackEmoji','nature')
    activity              : fake('slackEmoji','activity')
----------------------------------------
stock
    nyseSymbol            : fake('stock','nyseSymbol')
    nsdqSymbol            : fake('stock','nsdqSymbol')
----------------------------------------
shakespeare
    kingRichardIIIQuote   : fake('shakespeare','kingRichardIIIQuote')
    romeoAndJulietQuote   : fake('shakespeare','romeoAndJulietQuote')
    asYouLikeItQuote      : fake('shakespeare','asYouLikeItQuote')
    hamletQuote           : fake('shakespeare','hamletQuote')
----------------------------------------
cat
    name                  : fake('cat','name')
    registry              : fake('cat','registry')
    breed                 : fake('cat','breed')
----------------------------------------
beer
    name                  : fake('beer','name')
    style                 : fake('beer','style')
    hop                   : fake('beer','hop')
    yeast                 : fake('beer','yeast')
    malt                  : fake('beer','malt')
----------------------------------------
superhero
    name                  : fake('superhero','name')
    prefix                : fake('superhero','prefix')
    descriptor            : fake('superhero','descriptor')
    suffix                : fake('superhero','suffix')
    power                 : fake('superhero','power')
----------------------------------------
educator
    university            : fake('educator','university')
    secondarySchool       : fake('educator','secondarySchool')
    course                : fake('educator','course')
    campus                : fake('educator','campus')
----------------------------------------
team
    name                  : fake('team','name')
    state                 : fake('team','state')
    creature              : fake('team','creature')
    sport                 : fake('team','sport')
----------------------------------------
university
    name                  : fake('university','name')
    prefix                : fake('university','prefix')
    suffix                : fake('university','suffix')
----------------------------------------
space
    company               : fake('space','company')
    agencyAbbreviation    : fake('space','agencyAbbreviation')
    distanceMeasurement   : fake('space','distanceMeasurement')
    meteorite             : fake('space','meteorite')
    nebula                : fake('space','nebula')
    nasaSpaceCraft        : fake('space','nasaSpaceCraft')
    starCluster           : fake('space','starCluster')
    galaxy                : fake('space','galaxy')
    moon                  : fake('space','moon')
    constellation         : fake('space','constellation')
    agency                : fake('space','agency')
    star                  : fake('space','star')
    planet                : fake('space','planet')
----------------------------------------
demographic
    educationalAttainment : fake('demographic','educationalAttainment')
    race                  : fake('demographic','race')
    demonym               : fake('demographic','demonym')
    sex                   : fake('demographic','sex')
    maritalStatus         : fake('demographic','maritalStatus')
----------------------------------------
```