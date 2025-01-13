def enum_list(api_enums: list[str]):
    output = [f"@SerializedName(\"{e}\") {e.capitalize()}," for e in api_enums]

    for e in output:
        print(e)

    return output


def join_args(s):
    lines = [l.strip() for l in s.split("\n") if l.strip()]
    lines = [re.sub(r"^\[?.*? (.*?)( = .*)?\]?$", r"\1", l) for l in lines]
    print("|".join(lines))
